export interface Usuari {
  id: number;
  email: string;
  nom: string;
  cognom1: string;
  cognom2: string;
  nomComplet: string;
  esProfessor?: boolean;
  esAlumne?: boolean;
  label?: string;
  value?: string;
}
