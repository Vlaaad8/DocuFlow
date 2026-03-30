import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {UserStoredValue} from '../../model/ExtractedField';

@Component({
  selector: 'app-editable-field',
  templateUrl: './editable-field.component.html',
  styleUrls: ['./editable-field.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class EditableFieldComponent implements OnInit {
  @Input({required: true}) value!: UserStoredValue
  editable: boolean = false;

  @Output() saved = new EventEmitter<{ fieldID: number; value: string }>();

  editing: boolean = false;
  draft: string = '';


  ngOnInit(){
    this.editable = this.isEditable(this.value.fieldName);
  }
  private importanceMap: { [key: string]: number } = {
    'Personal Number': 3,
    'Address': 7,
  };

  startEdit() {
    if (!this.editable) return;
    this.draft = this.value.value ?? '';
    this.editing = true;
  }

  cancel() {
    this.editing = false;
    this.draft = '';
  }

  save() {
    this.editing = false;
    const newValue = this.draft ?? '';
    if (newValue !== this.value.value) {
      this.saved.emit({ fieldID: this.value.id, value: newValue });
    }
  }

  onDblClick() {
    this.startEdit();
  }

  formatSourceOfData(source: string): string {
    switch (source) {
      case("PASSPORT"):
        return 'Passport';
      case("NATIONAL_IDENTITY_CARD"):
        return 'Identity Card';
      case("MANUAL_ENTRY"):
        return 'Manual Entry';
      default:
        return source;
    }
  }

  isEditable(field: string): boolean {
    return this.importanceMap[field] != undefined;
  }
}
