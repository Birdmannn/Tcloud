package sia.tcloud3.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cascade;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor()
@Entity
public class Cart {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @JsonIgnore
    Long userId;

    @OneToMany(cascade = CascadeType.PERSIST)
    List<CartItem> cartItems;

    @Transient
    public void addItem(CartItem item) {
        cartItems.add(item);
    }
}