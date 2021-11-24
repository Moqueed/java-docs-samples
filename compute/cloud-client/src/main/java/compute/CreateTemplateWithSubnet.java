/*
 * Copyright 2021 Google LLC
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

package compute;

import com.google.cloud.compute.v1.AttachedDisk;
import com.google.cloud.compute.v1.AttachedDiskInitializeParams;
import com.google.cloud.compute.v1.GlobalOperationsClient;
import com.google.cloud.compute.v1.InsertInstanceTemplateRequest;
import com.google.cloud.compute.v1.InstanceProperties;
import com.google.cloud.compute.v1.InstanceTemplate;
import com.google.cloud.compute.v1.InstanceTemplatesClient;
import com.google.cloud.compute.v1.NetworkInterface;
import com.google.cloud.compute.v1.Operation;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class CreateTemplateWithSubnet {

  public static void main(String[] args)
      throws IOException, ExecutionException, InterruptedException {
    /*
    TODO(developer): Replace these variables before running the sample.
    projectId: project ID or project number of the Cloud project you use.
    network: the network to be used in the new template. This value uses
        the following format: "projects/{project}/global/networks/{network}"
    subnetwork: the subnetwork to be used in the new template. This value
        uses the following format: "projects/{project}/regions/{region}/subnetworks/{subnetwork}"
    templateName: name of the new template to create.
    */
    String projectId = "your-project-id";
    String network = "projects/{project}/global/networks/{network}";
    String subnetwork = "projects/{project}/regions/{region}/subnetworks/{subnetwork}";
    String templateName = "template-name";
    createTemplateWithSubnet(projectId, network, subnetwork, templateName);
  }

  // Create an instance template that uses a provided subnet.
  public static void createTemplateWithSubnet(String projectId, String network, String subnetwork,
      String templateName)
      throws IOException, ExecutionException, InterruptedException {
    try (InstanceTemplatesClient instanceTemplatesClient = InstanceTemplatesClient.create();
        GlobalOperationsClient globalOperationsClient = GlobalOperationsClient.create()) {

      AttachedDisk disk = AttachedDisk.newBuilder()
          .setInitializeParams(AttachedDiskInitializeParams.newBuilder()
              .setSourceImage("projects/debian-cloud/global/images/family/debian-11")
              .setDiskSizeGb(250).build())
          .setAutoDelete(true)
          .setBoot(true).build();

      InstanceProperties instanceProperties = InstanceProperties.newBuilder()
          .addDisks(disk)
          .setMachineType("e2-standard-4")
          .addNetworkInterfaces(NetworkInterface.newBuilder()
              .setNetwork(network)
              .setSubnetwork(subnetwork).build()).build();

      InstanceTemplate instanceTemplate = InstanceTemplate.newBuilder()
          .setName(templateName)
          .setProperties(instanceProperties).build();

      InsertInstanceTemplateRequest insertInstanceTemplateRequest = InsertInstanceTemplateRequest
          .newBuilder()
          .setProject(projectId)
          .setInstanceTemplateResource(instanceTemplate).build();

      Operation operation = instanceTemplatesClient.insertCallable()
          .futureCall(insertInstanceTemplateRequest).get();

      Operation response = globalOperationsClient.wait(projectId, operation.getName());

      if (response.hasError()) {
        System.out.println("Template creation from subnet failed ! ! " + response);
        return;
      }
      System.out.printf("Template creation from subnet operation status %s: %s", templateName,
          response.getStatus());
    }
  }
}
