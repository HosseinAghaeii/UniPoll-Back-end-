package ir.segroup.unipoll.ws.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="instructor_course")
public class InstructorCourseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String publicId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "course_id")
    private CourseEntity courseEntity;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "instructor_id")
    private InstructorEntity instructorEntity;

    private String description;

    @OneToMany(mappedBy = "instructorCourseEntity",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private List<BookletEntity> bookletEntities;

    @OneToMany(mappedBy = "instructorCourseEntity",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private List<RateEntity> rateEntities;

    @OneToMany(mappedBy = "icEntity",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private List<CommentCEntity> commentCEntities;
}
