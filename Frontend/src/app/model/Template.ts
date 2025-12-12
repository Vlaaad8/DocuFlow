import { Field } from "./Field";

export interface Template{
    id: number;
    name: string;
    category: string;
    status: string;
    fields: Field[];
}