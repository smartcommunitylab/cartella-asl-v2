export interface IPagedAzienda { 

    totalPages: number;
    totalElements: number;
    content: Azienda[];   

}

export class Azienda {
    id;
    address?: string;
    description?: string;
    email?: string;
    latitude: number;
    longitude: number;
    nome?: string;
    origin?: string;
    partita_iva?: string;
    pec?: string;
    phone?: string;
    idTipoAzienda;
    coordinate;
}