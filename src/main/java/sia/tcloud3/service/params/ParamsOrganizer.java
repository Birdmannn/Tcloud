package sia.tcloud3.service.params;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class ParamsOrganizer {

    public static Pageable organizePage(String sort, int page, int size) {
        String[] sortParam = null;
        if (sort.contains(","))
            sortParam = sort.split(",");

        sort = sortParam == null ? sort : sortParam[0];
        String direction = sortParam == null ? "desc" : sortParam[1];   // desc as default.
        Sort.Direction sortDirection = direction.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        return PageRequest.of(page, size, Sort.by(sortDirection, sort));
    }
}
