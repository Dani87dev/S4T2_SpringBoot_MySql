package cat.itacademy.s04.t02.n02.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fruits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fruit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int weightInKilos;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;
}