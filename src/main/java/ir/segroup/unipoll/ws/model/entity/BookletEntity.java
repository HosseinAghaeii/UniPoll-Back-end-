package ir.segroup.unipoll.ws.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "booklets")
public class BookletEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String publicId;

    private String filePath;

    private String text;

    private String term; // ##-#  year- 1 OR 2

    @ManyToMany(mappedBy = "favoriteBooklets", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserEntity> favoritedUsers; // users who have selected this booklet as they favorite booklet

    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(
            name = "likes",
            joinColumns = @JoinColumn(name = "booklet_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<UserEntity> likes;

    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "uploader_id")
    private UserEntity uploaderUser;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ic_id")
    private InstructorCourseEntity instructorCourseEntity;

    @OneToMany(mappedBy = "bookletEntity",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private List<CommentBEntity> commentBEntities;
}
