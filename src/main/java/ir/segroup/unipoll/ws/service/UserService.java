package ir.segroup.unipoll.ws.service;

import ir.segroup.unipoll.shared.model.BaseApiResponse;
import ir.segroup.unipoll.ws.model.request.UserRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<BaseApiResponse> createUser(UserRequest userRequest);
    ResponseEntity<BaseApiResponse> getAllUsers();
}
