create table if not exists `user`
(
    id        binary(16)   unique not null default (UUID_TO_BIN(UUID(), true)),
    username  varchar(50)  unique not null,
    firstname varchar(255),
    lastname  varchar(255),
    avatar    longblob,
    primary key (id)
    );


delete from `rococo-gateway`.`user`;

INSERT INTO `rococo-gateway`.`user` (id,username,firstname,lastname,avatar) VALUES
	 (0x11F10A772EE8DDBEAD860ECD2164492E,'TestDefaultUser','Test','User',NULL);
