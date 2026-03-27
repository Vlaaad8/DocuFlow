import { Template } from "./Template";

export interface GenerateTemplate {
    template: Template;
    canGenerate: boolean;
    missingFields: string[];
    dateFields: string[];
}
export interface TemplateApprovers {
    approverName: string;
    approverRole: string;
}
