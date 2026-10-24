package in.ybuilds.veritrust.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "credentials",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_credentials_credential_id",
                        columnNames = "credential_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "credential_id",
            nullable = false,
            length = 100
    )
    private String credentialId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "student_id",
            nullable = false
    )
    private User student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "institution_id",
            nullable = false
    )
    private Institution institution;

    @Column(
            name = "credential_type",
            nullable = false,
            length = 100
    )
    private String credentialType;

    @Column(
            nullable = false,
            length = 255
    )
    private String title;

    @Column(
            length = 1000
    )
    private String description;

    @Column(
            name = "issue_date",
            nullable = false
    )
    private LocalDate issueDate;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private CredentialStatus status;

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

    @Column(
            name = "commitment",
            nullable = false,
            length = 66
    )
    private String commitment;

    @Column(
            name = "blockchain_tx_hash",
            nullable = false,
            length = 66
    )
    private String blockchainTxHash;

    @Column(
            name = "blockchain_issuer",
            nullable = false,
            length = 42
    )
    private String blockchainIssuer;

    @Column(
            name = "blockchain_block_number",
            nullable = false
    )
    private BigInteger blockchainBlockNumber;

    @Column(name = "signature", length = 128)
    private String signature;

    @Column(name = "issuer_public_key", length = 100)
    private String issuerPublicKey;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = CredentialStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}