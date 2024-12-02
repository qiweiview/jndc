//分页对象
export type Page<T> = {
  records: Array<T>;
  total: number;
  size: number;
  current: number;
};
