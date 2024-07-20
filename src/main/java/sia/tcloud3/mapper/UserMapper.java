package sia.tcloud3.mapper;

import org.mapstruct.*;
import sia.tcloud3.dtos.response.GetUserResponse;
import sia.tcloud3.entity.Users;
import sia.tcloud3.dtos.requests.UserUpdateRequest;

@Mapper(componentModel = "spring")
public interface UserMapper {
    GetUserResponse toGetUserResponse(Users user);

//    @Mappings(implicit = true, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "password", ignore = true)
    void updateUser(@MappingTarget Users user, UserUpdateRequest request);
}
