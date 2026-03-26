import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'formatLabel'
})
export class FormatLabelPipe implements PipeTransform {

  transform(value: string): string {
    if (!value) {
      return '';
    }


    let separated = value.replace(/([a-z])([A-Z])/g, '$1 $2');
    separated = separated.charAt(0).toUpperCase() + separated.slice(1);

    return separated;
  }

}
