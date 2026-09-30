package in.ybuilds.veritrust.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "institutions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_institutions_code",
                        columnNames = "institution_code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Institution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;

    @Column(
            name = "institution_code",
            nullable = false,
            length = 50
    )
    private String institutionCode;

    @Column(
            name = "legal_name",
            nullable = false,
            length = 255
    )
    private String legalName;

    @Column(
            length = 500
    )
    private String address;

    @Column(
            length = 100
    )
    private String country;

    @Column(
            nullable = false
    )
    private boolean verified;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
