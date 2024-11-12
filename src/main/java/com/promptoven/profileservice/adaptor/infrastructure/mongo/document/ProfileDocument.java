package com.promptoven.profileservice.adaptor.infrastructure.mongo.document;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Document(collection = "profiles")
public class ProfileDocument {
    @Id
    private String memberUUID;  // Using memberUUID as the primary key
    
    @Indexed(unique = true)
    private String nickname;
    
    @Indexed(sparse = true)
    private String xId;
    
    @Indexed(sparse = true)
    private String instagramId;
    
    @Indexed(sparse = true)
    private String youtubeHandle;
    
    private String webLink;
    private String bio;
    private String banner;
    private String profileImage;
    
    @Indexed
    private boolean isCreator;
    
    @Indexed
    private ProfileStatus status;
    
    private ProfileVisibility visibility;
    
    @Indexed
    private int followerCount;
    private int followingCount;
    
    private List<String> followers;
    private List<String> following;
    private List<String> productIds;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    private LocalDateTime bannedAt;
    private LocalDateTime withdrawnAt;
    private String banReason;

    public enum ProfileStatus {
        ACTIVE,
        BANNED,
        WITHDRAWN
    }

    public enum ProfileVisibility {
        PUBLIC,    // Available for creators only
        PRIVATE,   // Available for non-creators
        HIDDEN     // For banned or withdrawn profiles
    }

    // Validation methods
    public boolean canBePublic() {
        return isCreator && status == ProfileStatus.ACTIVE;
    }

    public boolean canBePrivate() {
        return !isCreator && status == ProfileStatus.ACTIVE;
    }

    public boolean isAccessible() {
        return status == ProfileStatus.ACTIVE &&
            (visibility == ProfileVisibility.PUBLIC || 
             (visibility == ProfileVisibility.PRIVATE && isCreator));
    }

    // State change methods
    public void ban(String reason) {
        this.status = ProfileStatus.BANNED;
        this.visibility = ProfileVisibility.HIDDEN;
        this.bannedAt = LocalDateTime.now();
        this.banReason = reason;
    }

    public void withdraw() {
        this.status = ProfileStatus.WITHDRAWN;
        this.visibility = ProfileVisibility.HIDDEN;
        this.withdrawnAt = LocalDateTime.now();
    }

    public void activate() {
        this.status = ProfileStatus.ACTIVE;
        this.visibility = isCreator ? ProfileVisibility.PUBLIC : ProfileVisibility.PRIVATE;
        this.bannedAt = null;
        this.withdrawnAt = null;
        this.banReason = null;
    }

    // Existing follower management methods
    public void incrementFollowerCount() {
        this.followerCount++;
    }

    public void decrementFollowerCount() {
        if (this.followerCount > 0) {
            this.followerCount--;
        }
    }

    public void incrementFollowingCount() {
        this.followingCount++;
    }

    public void decrementFollowingCount() {
        if (this.followingCount > 0) {
            this.followingCount--;
        }
    }

    public void addFollower(String followerId) {
        if (this.followers == null) {
            this.followers = List.of(followerId);
        } else if (!this.followers.contains(followerId)) {
            this.followers.add(followerId);
            incrementFollowerCount();
        }
    }

    public void removeFollower(String followerId) {
        if (this.followers != null && this.followers.remove(followerId)) {
            decrementFollowerCount();
        }
    }

    public void addFollowing(String followingId) {
        if (this.following == null) {
            this.following = List.of(followingId);
        } else if (!this.following.contains(followingId)) {
            this.following.add(followingId);
            incrementFollowingCount();
        }
    }

    public void removeFollowing(String followingId) {
        if (this.following != null && this.following.remove(followingId)) {
            decrementFollowingCount();
        }
    }
}
