package ir.segroup.unipoll.ws.model.response;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private String text;
    private String publicId;
    private String createdDate;
    private String writerName;
    private String writerType;
    private String term;
}
