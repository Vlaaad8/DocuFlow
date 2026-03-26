import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'importance'
})
export class ImportancePipe implements PipeTransform {
  private importanceMap: { [key: string]: number } = {
    'FirstName': 1,
    'LastName': 2,
    'PersonalNumber': 3,
    'DateOfBirth': 4,
    'PlaceOfBirth': 5,
    'Sex': 6,
    'Address': 7,
    'Nationality': 8,
    'DocumentNumber': 9,
    'DocumentDiscriminator': 10,
    'DocumentType': 11,
    'DateOfIssue': 12,
    'DateOfExpiration': 13,
    'PlaceOfIssue': 14,
    'IssuingAuthority': 15
  };
  transform(value: string, ): number {
    return this.importanceMap[value] || 0;
  }

}
