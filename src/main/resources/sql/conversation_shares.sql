CREATE TABLE IF NOT EXISTS nbpuask.conversation_shares (
    share_id VARCHAR(80) NOT NULL COMMENT 'Public share id',
    conversation_id VARCHAR(64) NOT NULL COMMENT 'Source conversation id',
    owner_user_id BIGINT NOT NULL COMMENT 'Share owner user id',
    share_created_time VARCHAR(32) NOT NULL COMMENT 'Share created timestamp in milliseconds',
    share_available BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Whether this share is available',
    visit_count BIGINT NOT NULL DEFAULT 0 COMMENT 'Public visit count',
    last_visited_time VARCHAR(32) NULL COMMENT 'Last visited timestamp in milliseconds',
    PRIMARY KEY (share_id),
    UNIQUE KEY uk_conversation_share_conversation (conversation_id),
    KEY idx_conversation_share_owner (owner_user_id),
    KEY idx_conversation_share_available (share_available)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Conversation public shares';
