package ir.segroup.unipoll.ws.model.response;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactResponse {

    private String publicId;

    private String firstname;

    private String lastname;

    private String email;

    private  String text;
}
