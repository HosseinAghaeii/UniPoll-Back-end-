package ir.segroup.unipoll.ws.service.impl;

import ir.segroup.unipoll.config.exception.SystemServiceException;
import ir.segroup.unipoll.config.exception.constant.ExceptionMessages;
import ir.segroup.unipoll.shared.model.BaseApiResponse;
import ir.segroup.unipoll.shared.utils.BookletUtil;
import ir.segroup.unipoll.shared.utils.InstructorCourseUtil;
import ir.segroup.unipoll.ws.model.entity.InstructorCourseEntity;
import ir.segroup.unipoll.ws.model.response.BookletResponse;
import ir.segroup.unipoll.ws.model.response.InstructorCourseResponse;
import ir.segroup.unipoll.ws.model.response.UpdateICDescriptionResponse;
import ir.segroup.unipoll.ws.repository.InstructorCourseRepository;
import ir.segroup.unipoll.ws.service.InstructorCourseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static ir.segroup.unipoll.ws.repository.SpecificationImpl.InstructorCourseSpecificationImpl.fromCourseName;

@Service
public class InstructorCourseServiceImpl implements InstructorCourseService {
    private final InstructorCourseRepository instructorCourseRepository;

    private final InstructorCourseUtil instructorCourseUtil;

    private final BookletUtil bookletUtil;

    public InstructorCourseServiceImpl(InstructorCourseRepository instructorCourseRepository, InstructorCourseUtil instructorCourseUtil, BookletUtil bookletUtil) {
        this.instructorCourseRepository = instructorCourseRepository;
        this.instructorCourseUtil = instructorCourseUtil;
        this.bookletUtil = bookletUtil;
    }

    @Override
    public ResponseEntity<BaseApiResponse> findInstructorCourse(String filteredName) {
        Specification<InstructorCourseEntity> filters = Specification.where(StringUtils.isBlank(filteredName) ? null : fromCourseName(filteredName));
        List<InstructorCourseResponse> responses = instructorCourseRepository.findAll(filters)
                .stream()
                .map(instructorCourseUtil::convert)
                .toList();
        return instructorCourseUtil.createResponse(responses, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BaseApiResponse> getTenTopInstructorCourses() {
        HashMap<String,Double> rateMap = new HashMap<>();
        instructorCourseRepository.findAll()
                .forEach(course -> rateMap.put(course.getPublicId(),
                        instructorCourseUtil.calculateRate(course.getRateEntities())));
        List<String> tenTopPublicIdList = rateMap.entrySet()
                .stream()
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .limit(10)
                .map(Map.Entry::getKey)
                .toList();
        List<InstructorCourseResponse> responses = tenTopPublicIdList.stream()
                .map(publicId -> {
                    Optional<InstructorCourseEntity> instructorCourseEntity = instructorCourseRepository.findByPublicId(publicId);
                    return instructorCourseUtil.convert(instructorCourseEntity.get());
                })
                .toList();
        return instructorCourseUtil.createResponse(responses, HttpStatus.OK);
    }

    @Override

    public ResponseEntity<BaseApiResponse> getInstructorCourseBooklets(String token,String publicId) {
        String username = instructorCourseUtil.getUsernameFromToken(token);
        Optional<InstructorCourseEntity> instructorCourseEntity = instructorCourseRepository.findByPublicId(publicId);
        if (instructorCourseEntity.isEmpty())
            throw new SystemServiceException(ExceptionMessages.NO_RECORD_FOUND.getMessage(),HttpStatus.NOT_FOUND);

        List<BookletResponse> bookletResponse = instructorCourseEntity.get().getBookletEntities().stream()
                .map(be -> bookletUtil.convert(be, username))
                .toList();
        return bookletUtil.createResponse(bookletResponse,HttpStatus.OK);
    }
@Override
    public ResponseEntity<BaseApiResponse> getAInstructorCourse(String publicId) {
        Optional<InstructorCourseEntity> instructorCourseEntity = instructorCourseRepository.findByPublicId(publicId);
        if (instructorCourseEntity.isEmpty())
            throw new SystemServiceException(ExceptionMessages.NO_RECORD_FOUND.getMessage(),HttpStatus.NOT_FOUND);
        InstructorCourseResponse instructorCourseResponse = instructorCourseUtil.convert(instructorCourseEntity.get());
        return instructorCourseUtil.createResponse(instructorCourseResponse, HttpStatus.OK);
    }

  @Override
    public ResponseEntity<BaseApiResponse> editDescription(String publicId, String token,String newDescription) {
        String username = instructorCourseUtil.getUsernameFromToken(token);
        InstructorCourseEntity instructorCourseEntity =instructorCourseRepository.findByPublicId(publicId).orElseThrow(() ->
                new SystemServiceException(ExceptionMessages.NO_RECORD_FOUND.getMessage(), HttpStatus.NOT_FOUND)
                );
        if (!instructorCourseEntity.getInstructorEntity().getUsername().equals(username)){
            throw new SystemServiceException(ExceptionMessages.FORBIDDEN_EDIT_IC_DESCRIPTION_REQUEST.getMessage(),HttpStatus.FORBIDDEN);
        }
        instructorCourseEntity.setDescription(newDescription);
        instructorCourseEntity.setLastUpdate(instructorCourseUtil.getJalaliDate(new Date()));
        InstructorCourseEntity savedEntity ;
        try {
        savedEntity=instructorCourseRepository.save(instructorCourseEntity);
        }catch (Exception e){
            throw new SystemServiceException(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

        UpdateICDescriptionResponse response = UpdateICDescriptionResponse.builder()
                .description(savedEntity.getDescription())
                .lastUpdate(savedEntity.getLastUpdate())
                .build();

        return instructorCourseUtil.createResponse(response,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BaseApiResponse> isEnableToEdit(String publicId, String token) {
        String username = instructorCourseUtil.getUsernameFromToken(token);
        InstructorCourseEntity instructorCourseEntity =instructorCourseRepository.findByPublicId(publicId).orElseThrow(() ->
                new SystemServiceException(ExceptionMessages.NO_RECORD_FOUND.getMessage(), HttpStatus.NOT_FOUND)
        );
        if (!instructorCourseEntity.getInstructorEntity().getUsername().equals(username)){
            throw new SystemServiceException(ExceptionMessages.FORBIDDEN_EDIT_IC_DESCRIPTION_REQUEST.getMessage(),HttpStatus.FORBIDDEN);
        }
        return instructorCourseUtil.createResponse(instructorCourseEntity.getDescription(),HttpStatus.OK);
    }


}
