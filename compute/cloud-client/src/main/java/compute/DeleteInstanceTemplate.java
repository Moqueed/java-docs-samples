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

import com.google.cloud.compute.v1.DeleteInstanceTemplateRequest;
import com.google.cloud.compute.v1.GlobalOperationsClient;
import com.google.cloud.compute.v1.InstanceTemplatesClient;
import com.google.cloud.compute.v1.Operation;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class DeleteInstanceTemplate {

  public static void main(String[] args)
      throws IOException, ExecutionException, InterruptedException {
    // TODO(developer): Replace these variables before running the sample.
    // projectId: project ID or project number of the Cloud project you use.
    // templateName: name of the new template to create.
    String projectId = "your-project-id";
    String templateName = "template-name";
    deleteInstanceTemplate(projectId, templateName);
  }

  // Delete an instance template.
  public static void deleteInstanceTemplate(String projectId, String templateName)
      throws IOException, ExecutionException, InterruptedException {
    try (InstanceTemplatesClient instanceTemplatesClient = InstanceTemplatesClient.create();
        GlobalOperationsClient globalOperationsClient = GlobalOperationsClient.create()) {

      DeleteInstanceTemplateRequest deleteInstanceTemplateRequest = DeleteInstanceTemplateRequest.newBuilder()
          .setProject(projectId)
          .setInstanceTemplate(templateName).build();

      Operation operation = instanceTemplatesClient.deleteCallable().futureCall(deleteInstanceTemplateRequest).get();

      Operation response = globalOperationsClient.wait(projectId, operation.getName());

      if (response.hasError()) {
        System.out.println("Instance deletion failed ! ! " + response);
        return;
      }
      System.out.println("Instance deletion operation status: " + response.getStatus());
    }
  }
}