import { Field } from "./Field";

export interface Template{
    id: number;
    name: string;
    category: string;
    description: string;
    fields: Field[];
}


export interface ApprovalFlowTemplate {
    id : number;
    name: string;
}
