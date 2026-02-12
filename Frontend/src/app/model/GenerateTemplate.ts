import { Template } from "./Template";

export interface GenerateTemplate {
    template: Template;

    canGenerate: boolean;
}
export interface TemplateApprovers {
    approverName: string;
    approverRole: string;
}