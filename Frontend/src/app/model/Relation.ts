import { User } from "./User";

export interface Relation{
    id : number,
    boss: User,
    subordinate: User
}