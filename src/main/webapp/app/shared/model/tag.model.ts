import { ISqueak } from 'app/shared/model/squeak.model';

export interface ITag {
  id?: number;
  hashtag?: string;
  squeaks?: ISqueak[] | null;
}

export const defaultValue: Readonly<ITag> = {};
