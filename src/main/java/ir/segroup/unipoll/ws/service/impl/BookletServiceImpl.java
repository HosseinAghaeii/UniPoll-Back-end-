package ir.segroup.unipoll.ws.service.impl;

import ir.segroup.unipoll.config.exception.SystemServiceException;
import ir.segroup.unipoll.config.exception.constant.ExceptionMessages;
import ir.segroup.unipoll.shared.model.BaseApiResponse;
import ir.segroup.unipoll.shared.utils.BookletUtil;
import ir.segroup.unipoll.ws.model.entity.BookletEntity;
import ir.segroup.unipoll.ws.model.request.BookletRequest;
import ir.segroup.unipoll.ws.repository.BookletRepository;
import ir.segroup.unipoll.ws.service.BookletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;


@Service
public class BookletServiceImpl implements BookletService {
    private final BookletRepository bookletRepository;
    private final BookletUtil bookletUtil;
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public BookletServiceImpl(BookletRepository bookletRepository, BookletUtil bookletUtil) {
        this.bookletRepository = bookletRepository;
        this.bookletUtil = bookletUtil;
    }

    @Override
    public ResponseEntity<byte[]> downloadBooklet(String publicId) {
        Optional<BookletEntity> existedBooklet = bookletRepository.findByPublicId(publicId);
        if (existedBooklet.isEmpty())
            throw new SystemServiceException(ExceptionMessages.NO_RECORD_FOUND.getMessage(), HttpStatus.NOT_FOUND);
        String bookletPath = existedBooklet.get().getFilePath();
        try {
            byte[] booklet = Files.readAllBytes(new File(bookletPath).toPath());
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf(MediaType.APPLICATION_PDF_VALUE))
                    .body(booklet);
        }catch (IOException exception) {
            logger.error(exception.getMessage());
            throw new SystemServiceException(ExceptionMessages.FILE_EXCEPTION.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<BaseApiResponse> uploadBooklet(MultipartFile booklet, BookletRequest bookletRequest) {
        BookletEntity bookletEntity = bookletUtil.convert(booklet, bookletRequest);
        if (bookletEntity == null) {
            throw new SystemServiceException(ExceptionMessages.EMPTY_FILE.getMessage(), HttpStatus.NOT_FOUND);
        }
        try {
            BookletEntity savedBookletEntity = bookletRepository.save(bookletEntity);
            booklet.transferTo(new File(bookletEntity.getFilePath()));
            return bookletUtil.createResponse(savedBookletEntity.getFilePath(),HttpStatus.CREATED);
        }catch (IOException exception) {
            logger.error(exception.getMessage());
            throw new SystemServiceException(ExceptionMessages.FILE_EXCEPTION.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (Exception exception) {
            logger.error(exception.getMessage());
            throw new SystemServiceException(ExceptionMessages.DATABASE_IO_EXCEPTION.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<BaseApiResponse> getTenTopBooklets() {
        HashMap<String,Integer> likesNumberMap = new HashMap<>();
        bookletRepository.findAll().forEach(b -> likesNumberMap.put(b.getPublicId(),b.getLikes().size()));
        List<String> tenTopPublicId = likesNumberMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(10)
                .map(Map.Entry::getKey)
                .toList();
        return bookletUtil.createResponse(tenTopPublicId
//                                          .stream()
//                                          .map(bookletPublicId -> {
//                                              BookletEntity bookletEntity = bookletRepository.findByPublicId(bookletPublicId).get();
//                                          })
                ,HttpStatus.OK);
    }
}