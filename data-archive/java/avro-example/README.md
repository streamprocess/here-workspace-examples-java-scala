# Archiving SDII Stream Data in Avro

This example shows how to use the Data Archiving Library to quickly develop an archiving solution that archives data in Avro format.
There are two options for using this example:

1. Use the example as-is. You can run the example out of the box by creating an input and output catalog, then specifying those catalogs in the `application.conf` file. See the [Update the Configuration File](#update-the-configuration-file) section below for information on configuring this file.
2. Create your own archiving application using the example as a template.

You should review this entire readme regardless of whether you are running the example as-is or using the example as a template for your own application.

The example consists of two user defined function implementation example classes:

- AvroSimpleKeyExample.java
- AvroMultiKeysExample.java

These classes implement the SimpleUDF or MultiKeysUDF interface from the Data Archiving Library. You can choose which class to use depending on whether you want to use one value per indexing attribute (SimpleUDF) or multiple values per indexing attribute (MultiKeysUDF). See the **API Reference** section of the [Data Archiving Library Developer Guide](#data-archiving-library-developer-guide) for details on this and other interfaces.

This readme contains important instructions that will help you to run the **Data Archiving Library** examples.

## Get Your Credentials

To run this example, you need two sets of credentials:

* **Platform credentials:** To get access to the platform data and resources, including HERE Map Content data for your pipeline input. 
* **Repository credentials:** To download HERE Data SDK for Java & Scala libraries and Maven archetypes to your environment.

For more details on how to set up your credentials, see [Get Your Credentials](https://developer.here.com/documentation/java-scala-dev/dev_guide/topics/get-credentials.html).

For more details on how to verify that your platform credentials are configured correctly, see the [Verify Your Credentials](https://developer.here.com/documentation/java-scala-dev/dev_guide/verify-credentials/index.html) tutorial.

## Configure a Project

To follow this example, you'll need a [project](https://developer.here.com/documentation/access-control/user_guide/topics/manage-projects.html). A project is a collection of platform resources
 (catalogs, pipelines, and schemas) with controlled access. You can create a project through the
 **HERE platform portal**.
 
Alternatively, use the OLP CLI [`olp project create`](https://developer.here.com/documentation/open-location-platform-cli/user_guide/topics/project/project-commands.html#create-project) command to create the project:

```bash
olp project create $PROJECT_ID $PROJECT_NAME
```

The command returns the [HERE Resource Name (HRN)](https://developer.here.com/documentation/data-user-guide/user_guide/shared_content/topics/olp/concepts/hrn.html) of your new project. Note down this HRN as you'll need it later in this tutorial.

> Note:
> You don't have to provide a `--scope` parameter if your app has a default scope.
> For details on how to set a default project scope for an app, see the _Specify a
> default Project_ for Apps chapter of the [Teams and Permissions Guide](https://developer.here.com/documentation/access-control/user_guide/topics/manage-projects.html).

For more information on how to work with projects, see the [Organize your work in projects](https://developer.here.com/documentation/java-scala-dev/dev_guide/organize-work-in-projects/index.html) tutorial.

## Create Input Catalog and Layer

The examples require you to have a catalog with `stream` layer for the input data. Create a new catalog with `stream` layer or use an existing one.

Use the **HERE platform portal** to [create the input catalog](https://developer.here.com/documentation/data-user-guide/user_guide/portal/catalog-creating.html) in your project and [add the following layers](https://developer.here.com/documentation/data-user-guide/user_guide/portal/layer-creating.html):

| Layer ID               | Layer Type | Content Type             | Content Encoding | Coverage
|------------------------|------------|--------------------------|------------------|---------------
| stream 				 | Stream  	  | application/octet-stream | uncompressed     | -

- For instructions on how to create a catalog, refer to **Create a Catalog** in [Data User Guide](#data-user-guide).
- For instructions on how to create a layer, refer to **Create a Layer** in [Data User Guide](#data-user-guide).
- For instructions on how to link a resource to a project, refer to **Project Resources Link** in [Command Line Interface Developer Guide](#command-line-interface-developer-guide).
- For instructions on how to share your project, refer to **Manage Projects** in [Teams and Permissions User Guide](#teams-and-permissions-user-guide).

Alternatively, you can use the **OLP CLI Commands** in [Command Line Interface Developer Guide](#command-line-interface-developer-guide) instead of the platform portal to create a new catalog with a `stream` layer:

> Note:
> We recommend you to set values to variables so that you can easily copy and execute the following commands.

1. Use the [`olp catalog create`](https://developer.here.com/documentation/open-location-platform-cli/user_guide/topics/data/catalog-commands.html#catalog-create) command to create the catalog.
Make sure to note down the HRN returned by the following command for later use:
```bash
olp catalog create $CATALOG_ID $CATALOG_NAME --summary CATALOG_SUMMARY \
        --description CATALOG_DESCRIPTION --scope $PROJECT_HRN
```
2. Use the [`olp catalog layer add`](https://developer.here.com/documentation/open-location-platform-cli/user_guide/topics/data/layer-commands.html#catalog-layer-add) command to add a `stream` layer to your catalog:
```bash
olp catalog layer add $CATALOG_HRN stream stream --stream --summary "stream" \
        --description "stream" --content-type application/octet-stream \
        --scope $PROJECT_HRN
```

## Create Output Catalog and Layer

The examples require that you define indexing attributes with the following index definitions for the `index` layer:

- `ingestionTime` should be declared as `timewindow` type. There should be one, and ONLY one `timewindow` column  and it is mandatory for any `index` layer.
- `tileId` should be declared as `heretile` type. There should be at most one `heretile` column. It is optional.
- `eventType` should be declared as `string`.

Use the **HERE platform portal** to [create the output catalog](https://developer.here.com/documentation/data-user-guide/user_guide/portal/catalog-creating.html) in your project and [add the following layers](https://developer.here.com/documentation/data-user-guide/user_guide/portal/layer-creating.html):

| Layer ID               | Layer Type |  Retention | Timewindow Attribute Name  |	Duration | Content Type             | Content Encoding | Coverage
|------------------------|------------|------------|----------------------------|------------|--------------------------|------------------|---------------
| index                  | Index      |  7 days    | ingestionTime	            |   60       | application/x-avro-binary| uncompressed     | -

- For instructions on how to create a catalog, refer to **Create a Catalog** in [Data User Guide](#data-user-guide).
- For instructions on how to create a layer, refer to **Create a Layer** in [Data User Guide](#data-user-guide).
- For instructions on how to link a resource to a project, refer to **Project Resources Link** in [Command Line Interface Developer Guide](#command-line-interface-developer-guide).
- For instructions on how to share your project, refer to **Manage Projects** in [Teams and Permissions User Guide](#teams-and-permissions-user-guide).

Alternatively, you can use the **OLP CLI Commands** in [Command Line Interface Developer Guide](#command-line-interface-developer-guide) instead of the platform portal to create a new catalog with an `index` layer:

1. Use the [`olp catalog create`](https://developer.here.com/documentation/open-location-platform-cli/user_guide/topics/data/catalog-commands.html#catalog-create) command to create the catalog.
Make sure to note down the HRN returned by the following command for later use:
```bash
olp catalog create $CATALOG_ID $CATALOG_NAME --summary CATALOG_SUMMARY \
        --description CATALOG_DESCRIPTION --scope $PROJECT_HRN
```
2. Use the [`olp catalog layer add`](https://developer.here.com/documentation/open-location-platform-cli/user_guide/topics/data/layer-commands.html#catalog-layer-add) command to add an `index` layer to your catalog:
```bash
olp catalog layer add $CATALOG_HRN index index --index --summary "index" --description "index" \
        --content-type application/x-avro-binary --ttl 7.days \
        --index-definitions tileId:heretile:8 ingestionTime:timewindow:3600000 eventType:string \
        --scope $PROJECT_HRN
```

## Update the Configuration File

The configurations are specified in the `application.conf` file that can be found in the example's `/src/main/resources` directory of the example project. You will have to modify it to configure expected behavior of the data archiving project. For information about modifying this file, see the comments in the `application.conf` file.

> #### Note
> You must configure this file in order for the examples to run successfully.

> #### Note
> The property `aggregation.window-seconds` is different from the index attribute of type `timewindow`.
> The property `aggregation.window-seconds` determines how frequently data will be aggregated and processed by the data archiving pipeline.
> The index attribute of type `timewindow` contains the finest time granularity at which the data will be indexed and later queried, as specified in the attribute's `duration` field.

## Package the Application Into a Fat JAR

To run archiving pipeline in the HERE platform, you need to build a fat JAR.

You can refer to the `pom.xml` file for packaging details if you are creating your own application. If you are not creating your own application, run the following command under the example's base directory.
```bash
mvn clean package
```

Once the above command is successful, a fat JAR named `data-archive-avro-example-<VERSION>-platform.jar` will be built in the `target` folder.

## Create a Pipeline in the Platform

To run the example, create a pipeline in the HERE platform to execute the application.

### Configure and Run as a Platform Pipeline

You should use application jar with suffix `platform.jar` as a pipeline template to run the archiving process. The pipeline will use the configuration from file `application.conf` in the src/main/resource/
directory, ensure these values are updated before compiling and uploading your jar.

#### Using the Platform Portal to Run a Pipeline

For information on using the platform portal to configure and run a pipeline, see **Deploying a Pipeline via Web Platform Portal** in [Pipelines Developer Guide](#pipelines-developer-guide). Update the logging level of your pipeline from `WARN` to `INFO` if you intend to verify message upload in logs.

#### Use the Command Line Interface to Run a Pipeline

You can use the **OLP CLI Commands** in [Command Line Interface Developer Guide](#command-line-interface-developer-guide) to create pipeline components and activate it.

First, configure data sources using the `config/pipeline-config.conf` file. This file contains the configuration of the data sources which are used as placeholders for Data Archiving Library examples:

```javascript
pipeline.config {
  output-catalog {hrn = "YOUR_OUTPUT_CATALOG_HRN"}
  input-catalogs {
    source {hrn = "YOUR_INPUT_CATALOG_HRN"}
  }
}
```

You must replace `YOUR_OUTPUT_CATALOG_HRN` with the HRN of your output catalog and `YOUR_INPUT_CATALOG_HRN` with the HRN of your input catalog. To find the HRN, in the [HERE platform portal](#here-platform-portal), navigate to your catalog. The HRN is displayed in the upper left corner of the page.

You can use the OLP CLI to create pipeline components and activate the pipeline version with the following commands:

1. [Create](https://developer.here.com/documentation/open-location-platform-cli/user_guide/topics/pipeline-workflows.html) pipeline components:

```bash
olp pipeline create $PIPELINE_NAME --scope $PROJECT_HRN
olp pipeline template create $PIPELINE_TEMPLATE_NAME stream-3.0.0 $PATH_TO_JAR com.here.platform.dal.DALMain \
    --input-catalog-ids=source --workers=2 --scope $PROJECT_HRN # Note that the value of workers should be greater than or equal to the value of parallelism selected in application.conf
olp pipeline version create $PIPELINE_VERSION_NAME $PIPELINE_ID $PIPELINE_TEMPLATE_ID \
    "$PATH_TO_CONFIG_FOLDER/pipeline-config.conf" --scope $PROJECT_HRN
```

2. [Set](https://developer.here.com/documentation/open-location-platform-cli/user_guide/topics/pipeline/version-commands.html#pipeline-version-log-set-level) log level:

```bash
olp pipeline version log level set $PIPELINE_ID $PIPELINE_VERSION_ID --root info --scope $PROJECT_HRN
```

3. [Activate](https://developer.here.com/documentation/open-location-platform-cli/user_guide/topics/pipeline/version-commands.html#pipeline-version-activate) the pipeline version:

```bash
olp pipeline version activate $PIPELINE_ID $PIPELINE_VERSION_ID --scope $PROJECT_HRN
```

In the [HERE platform portal](https://platform.here.com/pipelines) / [HERE platform China portal](https://platform.hereolp.cn/pipelines)
navigate to your pipeline to see its status.

For more information on using the **OLP CLI** to configure and run a pipeline, see **Pipeline Commands** in [Command Line Interface Developer Guide](#command-line-interface-developer-guide).

> #### Note
> The selection of input and output catalog values using the portal when creating pipeline version or the values added to `pipeline.config` when using command line interface must be valid. These values represent a placeholder which will not be used by the the Data Archiving Library. Instead, values will be taken from the `application.conf` file. If you want to change or update the input/output catlogs, modify the `application.conf` file and rebuild.

## Verify the Output

In the [HERE platform portal](#here-platform-portal) select the _Pipelines_ tab and find your pipeline.
- Verify pipeline is in `Running` state.

After the pipeline is running, you can ingest your data into the `stream` layer created in the `Create Input Catalog and Layer` section using one of the following:
- **Publish to a Stream Layer** in [Data API Developer Guide](#data-api-developer-guide)
- **Publish Data** in [Data Client Library Developer Guide](#data-client-library-developer-guide)
- **Stream** in [Command Line Interface Developer Guide](#command-line-interface-developer-guide)

> #### Note
> For ingesting data, make sure your app has `read` and `write` permission to your `stream` layer.
> For instructions on how to manage app, refer to `Manage Apps` in [Teams and Permissions User Guide](#teams-and-permissions-user-guide).
  ```
  olp catalog permission grant $CATALOG_HRN --app $APP_ID --read --write --scope $PROJECT_HRN
  olp catalog layer stream put $CATALOG_HRN stream --input /path/to/directory --scope $PROJECT_HRN # There are sdii example messages in the folder: src/test/resources/sampleData
  ```

After your data is archived in `index` layer, you can query/retrieve data using one of the following:
- **Get Data from an Index Layer** in [Data API Developer Guide](#data-api-developer-guide)
- **Get Data** in [Data Client Library Developer Guide](#data-client-library-developer-guide)
- **Partitions** in [Command Line Interface Developer Guide](#command-line-interface-developer-guide)
> #### Note
> For querying metadata or retrieving data, make sure your app has `read` permission to your `index` layer.
> For instructions on how to manage app, refer to **Manage Apps** in [Teams and Permissions User Guide](#teams-and-permissions-user-guide).
  ```
  olp catalog permission grant $CATALOG_HRN --app $APP_ID --read --write --scope $PROJECT_HRN
  olp catalog layer partition list $CATALOG_HRN index --filter <query> --scope $PROJECT_HRN # E.g: <query>="tileId==92259"
  olp catalog layer partition get $CATALOG_HRN index --filter <query> --scope $PROJECT_HRN
  ```

To parse the data retrieved from `index` layer, refer to "How to parse the output content" in **FAQ** in [Data Archiving Library Developer Guide](#data-archiving-library-developer-guide).

## Run Example Locally

Besides running this example in a pipeline, you can also **Run an Archiving Application Locally**, refer to [Data Archiving Library Developer Guide](#data-archiving-library-developer-guide).
Running locally in your IDE or Flink cluster will use the configuration from file `application.conf` in the src/test/resource/ directory, ensure these values are updated before compiling and uploading your jar.
Refer to **Manage Apps** in [Teams and Permissions User Guide](#teams-and-permissions-user-guide) to create a new application and get `credentials.properties`. You will share read/write permissions with this app when you create your `stream` and `index` layers.

## Troubleshooting

If you have any trouble about accessing logs, monitoring, investigating failures and so on, refer to `FAQ` in [Data Archiving Library Developer Guide](#data-archiving-library-developer-guide).

## References

- ##### HERE Platform Portal
    * RoW: https://platform.here.com
    * China: https://platform.hereolp.cn
    
- ##### Data Archiving Library Developer Guide
     * RoW: https://developer.here.com/olp/documentation/data-archiving-library/dev_guide/index.html
     * China: https://developer.here.com/olp/cn/documentation/data-archiving-library/dev_guide/index.html
     
- ##### Command Line Interface Developer Guide
    * RoW: https://developer.here.com/olp/documentation/open-location-platform-cli/user_guide/index.html
    * China: https://developer.here.com/olp/cn/documentation/open-location-platform-cli/user_guide/index.html
    
- ##### Data API Developer Guide
     * RoW: https://developer.here.com/olp/documentation/data-api/data_dev_guide/index.html
     * China: https://developer.here.com/olp/cn/documentation/data-api/data_dev_guide/index.html
     
- ##### Data Client Library Developer Guide
     * RoW: https://developer.here.com/olp/documentation/data-client-library/dev_guide/index.html
     * China: https://developer.here.com/olp/cn/documentation/data-client-library/dev_guide/index.html
     
- ##### Data User Guide
    * RoW: https://developer.here.com/olp/documentation/data-user-guide/index.html
    * China: https://developer.here.com/olp/cn/documentation/data-user-guide/index.html
    
- ##### Pipelines Developer Guide
    * RoW: https://developer.here.com/olp/documentation/pipeline/index.html
    * China: https://developer.here.com/olp/cn/documentation/pipeline/index.html
    
- ##### Teams and Permissions User Guide
    * RoW: https://developer.here.com/olp/documentation/access-control/user-guide/index.html
    * China: https://developer.here.com/olp/cn/documentation/access-control/user-guide/index.html