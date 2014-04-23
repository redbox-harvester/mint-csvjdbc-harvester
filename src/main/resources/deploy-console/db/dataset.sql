/*
	Creating dataset table and importing dataset records.
*/
\p Dropping existing 'dataset' table...
\c true
DROP TABLE "dataset";
\c false

\p Creating table "dataset"...
CREATE TABLE "dataset" (
/*
	"datasetId" integer identity,
	"owner" varchar(20),
	"last_updated" timestamp
);
*/
"ID" integer,
"Name" varchar(80),
"Type" varchar(80),
"ANZSRC_FOR_1" varchar(80),
"ANZSRC_FOR_2" varchar(80),
"ANZSRC_FOR_3" varchar(80),
"Location" varchar(80),
"Coverage_Temporal_From" timestamp,
"Coverage_Temporal_To" timestamp,
"Coverage_Spatial_Type" timestamp,
"Coverage_Spatial_Value" timestamp,
"Existence_Start" timestamp,
"Existence_End" timestamp,
"Website" varchar(80),
"Data_Quality_Information" varchar(100),
"Reuse_Information" varchar(80),
"Access_Policy" varchar(80),
"URI" varchar(80),
"Description" varchar(80),
"Group" integer,
"datasetId" integer identity,
"owner" varchar(20),
"last_updated" timestamp
);


\p Inserting demo records into 'dataset'...
INSERT INTO "dataset" VALUES (1,'University of Examples - ReDBox, Metadata Registry','assemble','','','','123 Example Street, City, STATE',current_timestamp,current_timestamp,current_timestamp,current_timestamp,current_timestamp,current_timestamp,'http://service.example.edu.au/','http://service.example.edu.au/dataquality','http://service.example.edu.au/reuse','http://service.example.edu.au/access','','ReDBox is a metadata registry application for describing research data.',8, 1,'admin','2013-11-11 13:11:53');
/*
INSERT INTO "dataset" ("datasetId","owner","last_updated") VALUES (1,'admin','2013-11-11 13:11:53');
INSERT INTO "dataset" ("datasetId","owner","last_updated") VALUES (2,'admin','2013-11-12 10:27:52');
INSERT INTO "dataset" ("datasetId","owner","last_updated") VALUES (3,'admin','2013-11-12 10:27:56');
INSERT INTO "dataset" ("datasetId","owner","last_updated") VALUES (4,'admin','2013-11-12 10:28:01');
INSERT INTO "dataset" ("datasetId","owner","last_updated") VALUES (5,'admin','2013-11-12 10:28:05');
*/

COMMIT;
