import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

enum Api {
  list = '/approval/appProject/list',
  save = '/approval/appProject/add',
  edit = '/approval/appProject/edit',
  deleteOne = '/approval/appProject/delete',
  deleteBatch = '/approval/appProject/deleteBatch',
  importExcel = '/approval/appProject/importExcel',
  exportXls = '/approval/appProject/exportXls',
  queryAppRecordByMainId = '/approval/appProject/queryAppRecordByMainId',
}

/**
 * 列表查询
 * @param params
 */
export const list = (params) => defHttp.get({ url: Api.list, params });

/**
 * 删除单个
 */
export const deleteOne = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.deleteOne, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};

/**
 * 批量删除
 */
export const deleteBatch = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    onOk: () => {
      return defHttp.delete({ url: Api.deleteBatch, params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    },
  });
};

/**
 * 保存或者更新
 * @param params
 */
export const saveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.edit : Api.save;
  return defHttp.post({ url: url, params });
};

/**
 * 查询子表列表
 * @param params
 */
export const queryAppRecordByMainId = (params) => defHttp.get({ url: Api.queryAppRecordByMainId, params });
