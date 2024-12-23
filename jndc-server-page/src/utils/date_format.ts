export const formatDate = (dateStr: string): string => {
  if (!dateStr) {
    return "-";
  }
  const date = new Date(dateStr);

  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  const seconds = String(date.getSeconds()).padStart(2, "0");

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

export const calculateDateBetween = (
  connectTime: string,
  interruptTime: string
): string => {
  if (!connectTime) {
    return "-";
  }
  const date = new Date(connectTime);
  let now;
  if (interruptTime) {
    now = new Date(interruptTime);
  } else {
    now = new Date();
  }

  const diff = now.getTime() - date.getTime();
  const days = Math.floor(diff / (24 * 3600 * 1000));
  const leave1 = diff % (24 * 3600 * 1000);
  const hours = Math.floor(leave1 / (3600 * 1000));
  const leave2 = leave1 % (3600 * 1000);
  const minutes = Math.floor(leave2 / (60 * 1000));
  const leave3 = leave2 % (60 * 1000);
  const seconds = Math.round(leave3 / 1000);
  return `${days}天${hours}小时${minutes}分${seconds}秒`;
};
