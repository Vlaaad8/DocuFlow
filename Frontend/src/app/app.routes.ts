import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { UploadComponent } from './upload/upload.component';
import { LoginComponent } from './login/login.component';
import { TemplatesComponent } from './templates/templates.component';
import { GenerateComponent } from './generate/generate.component';
import { CreatorComponent } from './creator/creator.component';
import { MyProfileComponent } from './my-profile/my-profile.component';
import { HumanResourceComponent } from './humanResource/humanResource.component';
import { ApprovalFlowComponent } from './approvalFlow/approvalFlow.component';
import { RequestsComponent } from './requests/requests.component';

export const routes: Routes = [{
  path: 'dashboard', component: DashboardComponent,
}, {
  path: 'upload', component: UploadComponent
}
  , {
  path: 'login', component: LoginComponent
},
{
  path: '', component: LoginComponent
},
{
  path: 'template', component: TemplatesComponent
},
{
  path: 'generate', component: GenerateComponent
},
{
  path: 'template-creator', component: CreatorComponent
},
{
  path: 'template-creator/:id',
  component: CreatorComponent
},
{
  path: 'profile', component: MyProfileComponent
}
  ,
{ path: 'human-resource', component: HumanResourceComponent },
{ path: 'approval-flows', component: ApprovalFlowComponent },
{ path: 'requests', component: RequestsComponent }
];
