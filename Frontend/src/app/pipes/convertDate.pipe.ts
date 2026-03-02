import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'convertDate'
})
export class ConvertDatePipe implements PipeTransform {

  transform(value: string, args?: any): string {
    return value.split('T')[0];
  }

}
