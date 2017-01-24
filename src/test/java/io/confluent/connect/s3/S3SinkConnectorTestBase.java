/*
 * Copyright 2017 Confluent Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.confluent.connect.s3;

import org.junit.After;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.confluent.connect.avro.AvroData;
import io.confluent.connect.s3.format.avro.AvroFormat;
import io.confluent.connect.storage.StorageSinkTestBase;
import io.confluent.connect.storage.common.StorageCommonConfig;
import io.confluent.connect.storage.hive.HiveConfig;
import io.confluent.connect.storage.hive.schema.DefaultSchemaGenerator;
import io.confluent.connect.storage.partitioner.DefaultPartitioner;
import io.confluent.connect.storage.partitioner.PartitionerConfig;

public class S3SinkConnectorTestBase extends StorageSinkTestBase {

  protected static final String S3_TEST_URL = "http://127.0.0.1:8181";
  protected static final String S3_TEST_BUCKET_NAME = "kafka.bucket";

  protected S3SinkConnectorConfig connectorConfig;
  protected String topicsDir;
  protected AvroData avroData;
  protected Map<String, Object> rawConfig = new HashMap<>();

  @Override
  protected Map<String, String> createProps() {
    url = S3_TEST_URL;
    Map<String, String> props = super.createProps();
    props.put(StorageCommonConfig.STORAGE_CLASS_CONFIG, "io.confluent.connect.s3.storage.S3Storage");
    props.put(S3SinkConnectorConfig.S3_BUCKET_CONFIG, S3_TEST_BUCKET_NAME);
    props.put(S3SinkConnectorConfig.FORMAT_CLASS_CONFIG, AvroFormat.class.getName());
    props.put(PartitionerConfig.PARTITIONER_CLASS_CONFIG, PartitionerConfig.PARTITIONER_CLASS_DEFAULT.getName());
    props.put(PartitionerConfig.SCHEMA_GENERATOR_CLASS_CONFIG, DefaultSchemaGenerator.class.getName());
    props.put(StorageCommonConfig.DIRECTORY_DELIM_CONFIG, "_");
    return props;
  }

  //@Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    connectorConfig = new S3SinkConnectorConfig(properties);
    topicsDir = connectorConfig.getString(StorageCommonConfig.TOPICS_DIR_CONFIG);
    int schemaCacheSize = connectorConfig.getInt(S3SinkConnectorConfig.SCHEMA_CACHE_SIZE_CONFIG);
    avroData = new AvroData(schemaCacheSize);
    rawConfig = createDefaultConfig();
  }

  @After
  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }

  private Map<String, Object> createDefaultConfig() {
    Map<String, Object> config = new HashMap<>();
    config.put(PartitionerConfig.PARTITION_FIELD_NAME_CONFIG, "int");
    config.put(PartitionerConfig.PARTITION_DURATION_MS_CONFIG, TimeUnit.HOURS.toMillis(1));
    config.put(PartitionerConfig.PATH_FORMAT_CONFIG, "'year'=YYYY_'month'=MM_'day'=dd_'hour'=HH_");
    config.put(PartitionerConfig.LOCALE_CONFIG, "en");
    config.put(PartitionerConfig.TIMEZONE_CONFIG, "America/Los_Angeles");
    config.put(PartitionerConfig.SCHEMA_GENERATOR_CLASS_CONFIG, DefaultSchemaGenerator.class);
    config.put(StorageCommonConfig.DIRECTORY_DELIM_CONFIG, "_");
    return config;
  }

}

