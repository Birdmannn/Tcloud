package sia.tcloud3.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Type;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    Long assetId;

    String name;
    String type;
    String asset;

    @Lob
    @Column(columnDefinition = "image")
    byte[] imageData;
}
