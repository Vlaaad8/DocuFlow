import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { UploadComponent } from './upload/upload.component';
import { LoginComponent } from './login/login.component';
import { TemplatesComponent } from './templates/templates.component';
import { GenerateComponent } from './generate/generate.component';

export const routes: Routes = [{
  path: '',component: DashboardComponent,pathMatch: 'full',
},{
    path: 'upload',component:UploadComponent
}
,{
  path: 'login',component:LoginComponent
},
{
  path: 'template', component: TemplatesComponent
},
{
  path: 'generate' , component: GenerateComponent
}
   ];
