package hdscode.springrestfull.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchContactRequest {

    private String name;

    private String email;

    private String phone;

    @NotNull
    private Integer page;

    @NotNull
    private Integer size;
}
