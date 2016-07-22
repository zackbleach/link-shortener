# Link schema

# --- !Ups
create table `link` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `redirect_to` TEXT NOT NULL,
);

# --- !Downs
drop table `link`
