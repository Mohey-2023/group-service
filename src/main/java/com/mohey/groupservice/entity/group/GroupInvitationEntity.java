package com.mohey.groupservice.entity.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "invitation_tb")
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupInvitationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_uuid", nullable = false)
    private String groupUuid;

    @Column(name = "inviter_member_uuid", nullable = false)
    private String inviterMemberUuid;

    @Column(name = "invited_member_uuid", nullable = false)
    private String invitedMemberUuid;

    @Column(name = "invitation_time", nullable = false)
    private LocalDateTime invitationTime;
}
