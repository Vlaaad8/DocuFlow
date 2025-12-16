import { Component, OnInit } from '@angular/core';
import { MatSidenavModule } from "@angular/material/sidenav";
import { SidenavUserComponent } from "../commons/sidenav-user/sidenav-user.component";
import { ExitButtonComponent } from "../commons/exit-button/exit-button.component";
import { EditorComponent } from '@tinymce/tinymce-angular';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-creator',
  templateUrl: './creator.component.html',
  styleUrls: ['./creator.component.css'],
  imports: [MatSidenavModule, SidenavUserComponent, ExitButtonComponent,FormsModule, EditorComponent]
})
export class CreatorComponent implements OnInit {
 tinymceApiKey = 'ahh7gcufunnecgtf6i7axfxi4w4l5i9x02pq73y13qi80z0e'; // recomand: environment

  content = '<p>Create your template…</p>';

  tinyInit = {
    height: 600,
    menubar: false,
    plugins: 'lists link table code preview',
    toolbar: 'undo redo | bold italic | bullist numlist | link | table | code preview',
  };
  constructor() { }

  ngOnInit() {
  }

}
