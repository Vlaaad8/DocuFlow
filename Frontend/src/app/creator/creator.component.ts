import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSidenavModule } from "@angular/material/sidenav";
import { SidenavUserComponent } from "../commons/sidenav-user/sidenav-user.component";
import { ExitButtonComponent } from "../commons/exit-button/exit-button.component";
import { EditorComponent } from '@tinymce/tinymce-angular';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { TemplateService } from '../services/template.service';
import { MatProgressSpinner } from "@angular/material/progress-spinner";
import { CommonModule } from '@angular/common';
import { SnackBarService } from '../services/snackBar.service';
import { MatChipsModule } from "@angular/material/chips";
import { MatExpansionModule } from '@angular/material/expansion';
import { LabelDisplayComponent } from "../commons/label-display/label-display.component";
import { MatIconModule } from "@angular/material/icon";
import { CreatorService } from '../services/creator.service';
import { Field } from '../model/Field';


const defaultContent = '<p>Create your template…</p>';
@Component({
  selector: 'app-creator',
  templateUrl: './creator.component.html',
  styleUrls: ['./creator.component.css'],
  imports: [MatSidenavModule, SidenavUserComponent, ExitButtonComponent, FormsModule, EditorComponent, MatProgressSpinner, CommonModule, MatChipsModule, MatExpansionModule, LabelDisplayComponent, MatIconModule]
})
export class CreatorComponent implements OnInit {
  @ViewChild(EditorComponent) editorComp?: EditorComponent;
  public tinymceApiKey = 'ahh7gcufunnecgtf6i7axfxi4w4l5i9x02pq73y13qi80z0e';

  private path: string | null = null;
  public content = defaultContent

  public isEditorReady = false;
  private id: string | null = null;

  public fields: Field[] = [];

  public draggedContent: string = '';

  tinyInit = {
    height: 600,
    menubar: true,
    plugins: 'lists link table code preview',
    toolbar: 'undo redo | bold italic | bullist numlist | link | table | preview',
    inline_styles: true,
    extended_valid_elements: '*[class|style|id]',
    setup: (editor: any) => {

      editor.on('init', () => {

        this.isEditorReady = true;

        editor.on('dragover', (e: any) => {
          e.preventDefault();
          console.log('Dropped content:');
        });
        
        console.log('Editor is ready');
        if (this.content != defaultContent)
          editor.setContent(this.content);
      });

    }
  }


  constructor(private route: ActivatedRoute, private service: TemplateService, private snackBar: SnackBarService, private serviceCreator: CreatorService) { }

  ngOnInit() {
    this.id = this.route.snapshot.paramMap.get('id');
    if (this.id) {
      this.service.getTemplateHTMLById(+this.id).subscribe({
        next: (htmlContent) => {
          this.content = htmlContent.content;
          this.path = htmlContent.fileName;
        },
        error: (error) => {
          console.error('Error fetching template HTML:', error);
        }
      });
    }
    this.serviceCreator.getFields().subscribe({
      next: (fields) => {
        this.fields = fields;
        console.log('Fields fetched:', this.fields);
      },
      error: (error) => {
        console.error('Error fetching fields:', error);
      }
    });


  }

  handleEdit(): void {
    this.service.editTemplateHTML(this.content, this.path!).subscribe({
      next: () => {
        console.log('Template HTML updated successfully.');
      },
      error: (error) => {
        console.error('Error updating template HTML:', error);
      }
    });
  }

  showEditor(): boolean {
    if (this.id == null) {
      return this.isEditorReady && (this.fields.length > 0);
    }
    else {
      return this.isEditorReady && ((this.content != defaultContent) && (this.fields.length > 0));
    }
  }
  handleClear(): void {
    this.snackBar.showMessage('Clearing the editor will remove all unsaved changes. Are you sure?', 'success');
    const html = this.editorComp?.editor?.getContent({ format: 'html' });
    console.log(html);
  }

  handleDrag(representation: string): void {
    this.draggedContent = representation
  }
}