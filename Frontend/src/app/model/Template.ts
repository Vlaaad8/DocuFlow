import { Field } from "./Field";

export interface Template{
    id: number;
    name: string;
    category: string;
    description: string;
    status: string;
    fields: Field[];
}