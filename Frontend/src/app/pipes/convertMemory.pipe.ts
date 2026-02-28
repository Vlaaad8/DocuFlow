import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'convertMemory'
})
export class ConvertMemoryPipe implements PipeTransform {

  transform(bytes: number | null | undefined, decimals: number = 2): string {
    if (bytes === 0) return '0 Bytes';
    if (bytes === null || bytes === undefined || isNaN(bytes)) return '0 Bytes';
    if (!bytes || isNaN(bytes)) return '-';

    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB'];


    const i = Math.floor(Math.log(bytes) / Math.log(k));


    const unitIndex = Math.min(i, sizes.length - 1);

    const result = parseFloat((bytes / Math.pow(k, unitIndex)).toFixed(dm));

    return `${result} ${sizes[unitIndex]}`;
  }

}
