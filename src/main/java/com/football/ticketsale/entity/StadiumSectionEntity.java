package com.football.ticketsale.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
        name = "stadium_section",
        uniqueConstraints = {
                @UniqueConstraint(name = "ux_section_stadium_code", columnNames = {"stadium_uid", "section_code"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StadiumSectionEntity {

    @Id
    @Column(name = "section_uid", nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID sectionUid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stadium_uid", columnDefinition = "BINARY(16)",
            foreignKey = @ForeignKey(name = "fk_section_stadium"))
    private StadiumEntity stadium;

    @Column(name = "section_code", length = 10, nullable = false)
    private String sectionCode;

    @Column(name = "section_name", length = 60)
    private String sectionName;

    @Column(name = "stand_name", length = 60)
    private String standName;

    @Column(name = "seat_start", nullable = false)
    private Integer seatStart;

    @Column(name = "seat_end", nullable = false)
    private Integer seatEnd;

    @PrePersist
    public void ensureId() {
        if (sectionUid == null) sectionUid = UUID.randomUUID();
    }
}
