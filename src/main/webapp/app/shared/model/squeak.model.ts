import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { ITag } from 'app/shared/model/tag.model';
import { IMentions } from 'app/shared/model/mentions.model';

export interface ISqueak {
  id?: number;
  content?: string;
  imageContentType?: string | null;
  image?: string | null;
  created?: string | null;
  likes?: number | null;
  user?: IUser | null;
  tags?: ITag[] | null;
  mentions?: IMentions[] | null;
}

export const defaultValue: Readonly<ISqueak> = {};
