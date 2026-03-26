import {Component, EventEmitter, Input, Output} from '@angular/core';
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
export class EditableFieldComponent {
  @Input({required: true}) value!: UserStoredValue
  @Input() editable: boolean = true;

  @Output() saved = new EventEmitter<{ fieldKey: string; value: string }>();

  editing: boolean = false;
  draft: string = '';

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
    // this.editing = false;
    // const newValue = this.draft ?? '';
    // if (newValue !== this.value) {
    //   this.saved.emit({ fieldKey: this., value: newValue });
    // }
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
      default:
        return source;
    }
  }

}
