package sia.tcloud3.dtos.requests;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import sia.tcloud3.entity.Ingredient;

import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DesignRequest {
    String name;
    List<String> ingredientList;
}
