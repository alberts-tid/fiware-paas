--liquibase formatted sql

--changeset jesuspg:1-1
CREATE TABLE artifact_attribute (
  artifact_id   INT8 NOT NULL,
  attributes_id INT8 NOT NULL
);

--changeset jesuspg:1-2
CREATE TABLE metadata (
  id          INT8          NOT NULL,
  description VARCHAR(2048),
  key         VARCHAR(256)  NOT NULL,
  v           INT8,
  value       VARCHAR(2048) NOT NULL
);

--changeset jesuspg:1-3
CREATE TABLE network (
  id   INT8 NOT NULL,
  name VARCHAR(255),
  vdc  VARCHAR(255)
);

--changeset jesuspg:1-4
CREATE TABLE network_subnetwork (
  network_id INT8 NOT NULL,
  subnets_id INT8 NOT NULL
);

--changeset jesuspg:1-5
CREATE TABLE networkinstance (
  id           INT8 NOT NULL,
  adminstateup BOOL NOT NULL,
  idnetwork    VARCHAR(255),
  name         VARCHAR(255),
  netdefault   BOOL NOT NULL,
  shared       BOOL NOT NULL,
  subnetcount  INT4 NOT NULL,
  tenantid     VARCHAR(255),
  vdc          VARCHAR(255)
);

--changeset jesuspg:1-6
CREATE TABLE networkinstance_router (
  networkinstance_id INT8 NOT NULL,
  routers_id         INT8 NOT NULL
);

--changeset jesuspg:1-7
CREATE TABLE networkinstance_subnetworkinstance (
  networkinstance_id INT8 NOT NULL,
  subnets_id         INT8 NOT NULL
);

--changeset jesuspg:1-8
CREATE TABLE productrelease_metadata (
  productrelease_id INT8 NOT NULL,
  metadatas_id      INT8 NOT NULL
);

--changeset jesuspg:1-9
CREATE TABLE router (
  id              INT8 NOT NULL,
  idpublicnetwork VARCHAR(255),
  idrouter        VARCHAR(255),
  name            VARCHAR(255)
);

--changeset jesuspg:1-10
CREATE TABLE subnetwork (
  id   INT8 NOT NULL,
  cidr VARCHAR(255),
  name VARCHAR(255)
);

--changeset jesuspg:1-11
CREATE TABLE subnetworkinstance (
  id                   INT8 NOT NULL,
  allocationpoolsend   VARCHAR(255),
  allocationpoolsstart VARCHAR(255),
  cidr                 VARCHAR(255),
  idnetwork            VARCHAR(255),
  idsubnet             VARCHAR(255),
  name                 VARCHAR(255)
);

--changeset jesuspg:1-12
CREATE TABLE tier_has_networks (
  tier_id    INT8 NOT NULL,
  network_id INT8 NOT NULL
);

--changeset jesuspg:1-13
CREATE TABLE tierinstance_has_networkinstance (
  tierinstance_id    INT8 NOT NULL,
  networkinstance_id INT8 NOT NULL
);

--changeset jesuspg:1-14
ALTER TABLE tier ADD COLUMN region VARCHAR(255);

--changeset jesuspg:1-15
ALTER TABLE environment_has_tiers ADD CONSTRAINT environment_has_tiers_pkey PRIMARY KEY (environment_id, tier_id);

--changeset jesuspg:1-16
ALTER TABLE installableinstance_attribute ADD CONSTRAINT installableinstance_attribute_pkey PRIMARY KEY (installableinstance_id, privateattributes_id);

--changeset jesuspg:1-17
ALTER TABLE metadata ADD CONSTRAINT metadata_pkey PRIMARY KEY (id);

--changeset jesuspg:1-18
ALTER TABLE network ADD CONSTRAINT network_pkey PRIMARY KEY (id);

--changeset jesuspg:1-19
ALTER TABLE network_subnetwork ADD CONSTRAINT network_subnetwork_pkey PRIMARY KEY (network_id, subnets_id);

--changeset jesuspg:1-20
ALTER TABLE networkinstance ADD CONSTRAINT networkinstance_pkey PRIMARY KEY (id);

--changeset jesuspg:1-21
ALTER TABLE networkinstance_router ADD CONSTRAINT networkinstance_router_pkey PRIMARY KEY (networkinstance_id, routers_id);

--changeset jesuspg:1-22
ALTER TABLE networkinstance_subnetworkinstance ADD CONSTRAINT networkinstance_subnetworkinstance_pkey PRIMARY KEY (networkinstance_id, subnets_id);

--changeset jesuspg:1-23
ALTER TABLE productrelease_attribute ADD CONSTRAINT productrelease_attribute_pkey PRIMARY KEY (productrelease_id, attributes_id);

--changeset jesuspg:1-24
ALTER TABLE productrelease_metadata ADD CONSTRAINT productrelease_metadata_pkey PRIMARY KEY (productrelease_id, metadatas_id);

--changeset jesuspg:1-25
ALTER TABLE router ADD CONSTRAINT router_pkey PRIMARY KEY (id);

--changeset jesuspg:1-26
ALTER TABLE subnetwork ADD CONSTRAINT subnetwork_pkey PRIMARY KEY (id);

--changeset jesuspg:1-27
ALTER TABLE subnetworkinstance ADD CONSTRAINT subnetworkinstance_pkey PRIMARY KEY (id);

--changeset jesuspg:1-28
ALTER TABLE tier_has_networks ADD CONSTRAINT tier_has_networks_pkey PRIMARY KEY (tier_id, network_id);

--changeset jesuspg:1-29
ALTER TABLE tierinstance_has_networkinstance ADD CONSTRAINT tierinstance_has_networkinstance_pkey PRIMARY KEY (tierinstance_id, networkinstance_id);

--changeset jesuspg:1-30
ALTER TABLE networkinstance_router ADD CONSTRAINT fk_networkinstance_router_router FOREIGN KEY (routers_id)
REFERENCES router (id) NOT DEFERRABLE;

--changeset jesuspg:1-31
ALTER TABLE networkinstance_router ADD CONSTRAINT fk_networkinstance_router_networkinstance FOREIGN KEY (networkinstance_id)
REFERENCES networkinstance (id) NOT DEFERRABLE;

--changeset jesuspg:1-32
ALTER TABLE tierinstance_has_networkinstance ADD CONSTRAINT fk_tierinstance_has_networkinstance_tierinstance FOREIGN KEY (tierinstance_id)
REFERENCES tierinstance (id) NOT DEFERRABLE;

--changeset jesuspg:1-33
ALTER TABLE tierinstance_has_networkinstance ADD CONSTRAINT fk_tierinstance_has_networkinstance FOREIGN KEY (networkinstance_id)
REFERENCES networkinstance (id) NOT DEFERRABLE;

--changeset jesuspg:1-34
ALTER TABLE tier_has_networks ADD CONSTRAINT fk_tier_has_networks_tier FOREIGN KEY (tier_id)
REFERENCES tier (id) NOT DEFERRABLE;

--changeset jesuspg:1-35
ALTER TABLE tier_has_networks ADD CONSTRAINT fk_tier_has_networks_network FOREIGN KEY (network_id)
REFERENCES network (id) NOT DEFERRABLE;

--changeset jesuspg:1-36
ALTER TABLE networkinstance_subnetworkinstance ADD CONSTRAINT fk_networkinstance_subnetworkinstance_subnetworkinstance FOREIGN KEY (subnets_id)
REFERENCES subnetworkinstance (id) NOT DEFERRABLE;

--changeset jesuspg:1-37
ALTER TABLE networkinstance_subnetworkinstance ADD CONSTRAINT fk_networkinstance_subnetworkinstance_networkinstance FOREIGN KEY (networkinstance_id)
REFERENCES networkinstance (id) NOT DEFERRABLE;

--changeset jesuspg:1-38
ALTER TABLE productrelease_metadata ADD CONSTRAINT fk_productrelease_metadata_metadata FOREIGN KEY (metadatas_id)
REFERENCES metadata (id) NOT DEFERRABLE;

--changeset jesuspg:1-39
ALTER TABLE productrelease_metadata ADD CONSTRAINT fk_productrelease_metadata_productrelease FOREIGN KEY (productrelease_id)
REFERENCES productrelease (id) NOT DEFERRABLE;

--changeset jesuspg:1-40
ALTER TABLE artifact_attribute ADD CONSTRAINT fk_artifact_attribute_attribute FOREIGN KEY (attributes_id)
REFERENCES attribute (id) NOT DEFERRABLE;

--changeset jesuspg:1-41
ALTER TABLE artifact_attribute ADD CONSTRAINT fk_artifact_attribute_artifact FOREIGN KEY (artifact_id)
REFERENCES artifact (id) NOT DEFERRABLE;

--changeset jesuspg:1-42
ALTER TABLE network_subnetwork ADD CONSTRAINT fk_network_subnetwork_network FOREIGN KEY (network_id)
REFERENCES network (id) NOT DEFERRABLE;

--changeset jesuspg:1-43
ALTER TABLE network_subnetwork ADD CONSTRAINT fk_network_subnetwork_subnetwork FOREIGN KEY (subnets_id)
REFERENCES subnetwork (id) NOT DEFERRABLE;

--changeset jesuspg:1-44
ALTER TABLE artifact_attribute ADD CONSTRAINT artifact_attribute_attributes_id_key UNIQUE (attributes_id);

--changeset jesuspg:1-45
ALTER TABLE network_subnetwork ADD CONSTRAINT network_subnetwork_subnets_id_key UNIQUE (subnets_id);

--changeset jesuspg:1-46
ALTER TABLE networkinstance_router ADD CONSTRAINT networkinstance_router_routers_id_key UNIQUE (routers_id);

--changeset jesuspg:1-47
ALTER TABLE networkinstance_subnetworkinstance ADD CONSTRAINT networkinstance_subnetworkinstance_subnets_id_key UNIQUE (subnets_id);

--changeset jesuspg:1-48
ALTER TABLE productrelease_metadata ADD CONSTRAINT productrelease_metadata_metadatas_id_key UNIQUE (metadatas_id);

--changeset jesuspg:1-49
ALTER TABLE artifact_artifact DROP CONSTRAINT fk_artifact_artifact_artifact;

--changeset jesuspg:1-50
ALTER TABLE artifact_artifact DROP CONSTRAINT fk_artifact_artifact_artifact2;

--changeset jesuspg:1-51
ALTER TABLE environment DROP CONSTRAINT fk_environment_environmenttype;

--changeset jesuspg:1-52
ALTER TABLE applicationtype_environmenttype DROP CONSTRAINT fk_applicationtype_environmenttype_environmenttype;

--changeset jesuspg:1-53
ALTER TABLE applicationtype_environmenttype DROP CONSTRAINT fk_applicationtype_environmenttype_applicationtype;

--changeset jesuspg:1-54
ALTER TABLE applicationtype_environmenttype DROP CONSTRAINT applicationtype_environmenttype_environmenttypes_id_key;

--changeset jesuspg:1-55
ALTER TABLE artifact_artifact DROP CONSTRAINT artifact_artifact_attributes_id_key;

--changeset jesuspg:1-56
ALTER TABLE environmenttype DROP CONSTRAINT environmenttype_name_key;

--changeset jesuspg:1-57
DROP TABLE applicationtype_environmenttype;

--changeset jesuspg:1-58
DROP TABLE artifact_artifact;

--changeset jesuspg:1-59
DROP TABLE environmenttype;

--changeset jesuspg:1-60
ALTER TABLE environment DROP COLUMN environmenttype_id;
--changeset jesuspg:1-61
ALTER TABLE installableinstance ALTER COLUMN date TYPE TIMESTAMP WITHOUT TIME ZONE;

--changeset jesuspg:1-62
ALTER TABLE task ALTER COLUMN endtime TYPE TIMESTAMP WITHOUT TIME ZONE;

-- Change that corrresponds to bug/CLAUDIA-3652 Managing user attributes -->
--changeset jmms392:2-1
ALTER TABLE productrelease ADD COLUMN tiername VARCHAR(256);

-- Change that corresponds to bug/CLAUDIA3663-Invalidnamesforenvironmentandtiers -->
-- changeset henar:3-1
ALTER TABLE environmentinstance alter column description type  VARCHAR(256) ;
ALTER TABLE environmentinstance alter column blueprintname type  VARCHAR(256) ;


-- changeset jesuspg:4-1
ALTER TABLE tierinstance DROP COLUMN networks;

ALTER TABLE artifact DROP COLUMN artifacttype_id;

-- changeset henar:5-1 --
ALTER TABLE networkinstance ADD COLUMN external BOOL NOT NULL;


-- changeset henar:5-2 --
ALTER TABLE networkinstance ADD COLUMN region VARCHAR(255);
ALTER TABLE network ADD COLUMN region VARCHAR(255);
ALTER TABLE subnetwork ADD COLUMN region VARCHAR(255);
ALTER TABLE subnetwork ADD COLUMN vdc VARCHAR(255);
ALTER TABLE subnetworkinstance ADD COLUMN region VARCHAR(255);
ALTER TABLE subnetworkinstance ADD COLUMN vdc VARCHAR(255);


-- changeset henar:5-3 --
ALTER TABLE tier ADD COLUMN affinity VARCHAR(128);
DROP TABLE artifacttype;
DROP TABLE configuration;
DROP TABLE service_attribute;
DROP TABLE service;
ALTER TABLE applicationrelease drop constraint fk_applicationrelease_applicationtype;
ALTER TABLE productrelease DROP COLUMN producttype_id;
ALTER TABLE applicationrelease DROP COLUMN applicationtype_id;
DROP TABLE applicationtype;
DROP TABLE productype;

-- changeset henar:5-4 --
ALTER TABLE tierinstance ADD COLUMN floatingip VARCHAR(128);

-- changeset henar:5-5 --
ALTER TABLE network ADD COLUMN federatednetwork VARCHAR(128);
ALTER TABLE network ADD COLUMN federatedRange VARCHAR(128);
ALTER TABLE networkinstance ADD COLUMN federatednetwork VARCHAR(128);
ALTER TABLE networkinstance ADD COLUMN federatedRange VARCHAR(128);
UPDATE network set federatednetwork=false where federatednetwork is NULL;
UPDATE networkinstance set federatednetwork=false where federatednetwork is NULL;
UPDATE networkinstance set federatedRange='' where federatedRange is NULL;
UPDATE network set federatedRange='' where federatedRange is NULL;


