import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSidenavModule } from "@angular/material/sidenav";
import { SidenavUserComponent } from "../commons/sidenav-user/sidenav-user.component";
import { ExitButtonComponent } from "../commons/exit-button/exit-button.component";
import { EditorComponent } from '@tinymce/tinymce-angular';
import { FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
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
import { LoadingComponent } from "../commons/loading/loading.component";
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ApprovalFlowTemplate } from '../model/Template';
import {MatProgressBar} from '@angular/material/progress-bar';


const defaultContent = '<p>Create your template…</p>';
@Component({
  selector: 'app-creator',
  templateUrl: './creator.component.html',
  styleUrls: ['./creator.component.css'],
  imports: [MatSidenavModule, SidenavUserComponent, ExitButtonComponent, FormsModule, EditorComponent, CommonModule, MatChipsModule, MatExpansionModule, LabelDisplayComponent, MatIconModule, LoadingComponent, MatDialogModule, ReactiveFormsModule, MatProgressSpinner, MatProgressBar]
})
export class CreatorComponent implements OnInit {

  @ViewChild(EditorComponent) editorComp?: EditorComponent;
  @ViewChild('templateDetails') templateDetails!: any;

  public tinymceApiKey = 'ahh7gcufunnecgtf6i7axfxi4w4l5i9x02pq73y13qi80z0e';

  private path: string | null = null;
  public content = defaultContent

  public isEditorReady = false;
  private id: string | null = null;

  public fields: Field[] = [];

  public draggedContent: string = '';

  templateCategories!: string[];
  approvalFlows!: ApprovalFlowTemplate[];
  formGroup!: FormGroup
  errorMessage: string | null = null;

  tinyInit = {
    height: 600,
    menubar: true,
    plugins: 'lists link table code preview',
    content_style: "body { font-family: 'Times New Roman', Times, serif; font-size: 12pt; }",
    font_family_formats: "Times New Roman=times new roman,times,serif; Arial=arial,helvetica,sans-serif; Courier New=courier new,courier,monospace",
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
        editor.execCommand('fontName', false, 'Times New Roman');
        console.log('Editor is ready');
        if (this.content != defaultContent)
          editor.setContent(this.content);
      });

    }
  }


  constructor(private route: ActivatedRoute, private service: TemplateService, private snackBar: SnackBarService, private serviceCreator: CreatorService, private dialog: MatDialog) { }

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
        fields.sort((a, b) => a.required === b.required ? 0 : a.required ? -1 : 1);

        this.fields = fields;
        console.log('Fields fetched:', this.fields);
      },
      error: (error) => {
        console.error('Error fetching fields:', error);
      }
    });


  }

  handleEdit(): void {
    if (this.id == null) {
      this.handleSave();
    }
    else {
      this.service.editTemplateHTML(this.content, this.path!).subscribe({
        next: () => {
          this.snackBar.showMessage('Template updated successfully!', 'success');
          this.errorMessage = null;
          this.content = defaultContent;
          this.id = null;
          this.path = null;
        },
        error: (error) => {
          this.errorMessage = error.error;
        }
      });
    }
  }


  showEditor(): boolean {
    if (this.id == null) {
      return this.isEditorReady && (this.fields.length > 0);
    }
    else {
      return this.isEditorReady && ((this.content != "<p>Create your template&hellip;</p>") && (this.fields.length > 0));
    }
  }
  handleClear(): void {
    this.content = defaultContent;
  }

  handleDrag(representation: string): void {
    this.draggedContent = representation
  }

  handleSave(): void {
    this.service.validateHTMLTemplate(this.content).subscribe({
      next: () => {
        this.errorMessage = null;
        this.dialog.open(this.templateDetails);
      },
      error: (error) => {
        this.errorMessage = error.error;
      }
    });
  }


  closeDialog(): void {
    this.dialog.closeAll();
  }
}
