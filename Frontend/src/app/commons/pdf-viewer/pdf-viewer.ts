import {Component, EventEmitter, Input, Output} from '@angular/core';
import {PdfViewerModule} from 'ng2-pdf-viewer';
import {MatIconModule} from '@angular/material/icon';

@Component({
  selector: 'app-pdf-viewer',
  imports: [
    PdfViewerModule,
    MatIconModule,
  ],
  templateUrl: './pdf-viewer.html',
  styleUrl: './pdf-viewer.css',
})
export class PdfViewer {

  @Input() pdfUrl: string = '';
  @Output() closeViewer = new EventEmitter<void>();


  handleClose() : void{
    this.closeViewer.emit();

  }

}
