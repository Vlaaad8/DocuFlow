import { Field } from "./Field";

export interface Template{
    id: string;
    name: string;
    category: string;
    status: string;
    fields: Field[];
}