import { ISqueak } from 'app/shared/model/squeak.model';

export interface IMentions {
  id?: number;
  handle?: string;
  squeaks?: ISqueak[] | null;
}

export const defaultValue: Readonly<IMentions> = {};
