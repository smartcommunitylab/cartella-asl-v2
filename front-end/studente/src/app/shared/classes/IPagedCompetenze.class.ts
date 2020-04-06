import { Competenza } from "./Competenza.class";

export interface IPagedCompetenze { 

    totalPages: number;
    totalElements: number;
    content: Competenza[];

}