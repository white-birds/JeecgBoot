<template>
  <div class="p-4">
    <BasicTable @register="registerTable">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:plus-outlined" @click="handleCreate"> 发起申请</a-button>
      </template>
      <template #action="{ record }">
        <TableAction
          :actions="[
            {
              label: '修改',
              icon: 'ant-design:edit-outlined',
              onClick: handleEdit.bind(null, record),
              ifShow: record.status !== '已通过',
            },
            {
              label: '详情',
              icon: 'ant-design:eye-outlined',
              onClick: handleEdit.bind(null, record),
              ifShow: record.status === '已通过',
            },
            {
              label: record.status === '待审批' ? '撤回' : '删除',
              icon: record.status === '待审批' ? 'ant-design:undo-outlined' : 'ant-design:delete-outlined',
              color: 'error',
              popConfirm: {
                title: record.status === '待审批' ? '确认要撤回该申请吗？' : '是否确认删除该记录?',
                confirm: handleDelete.bind(null, record),
              },
              ifShow: record.status !== '已通过',
            },
          ]"
        />
      </template>
    </BasicTable>
    <AppProjectModal @register="registerModal" @success="reload" />
  </div>
</template>

<script lang="ts" setup>
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { list, deleteOne } from './approval.api';
  import { columns, searchFormSchema } from './approval.data';
  import AppProjectModal from './components/AppProjectModal.vue';
  import { useUserStore } from '/@/store/modules/user';

  const userStore = useUserStore();
  const [registerModal, { openModal }] = useModal();
  
  const [registerTable, { reload }] = useTable({
    title: '我的申请列表',
    api: list,
    beforeFetch: (params) => {
      // 过滤只看自己的申请
      return { ...params, applicant: userStore.getUserInfo.username };
    },
    columns,
    formConfig: {
      labelWidth: 100,
      schemas: searchFormSchema,
    },
    useSearchForm: true,
    showTableSetting: true,
    bordered: true,
    actionColumn: {
      width: 160,
      title: '操作',
      dataIndex: 'action',
      key: 'action',
      slots: { customRender: 'action' },
      fixed: 'right',
    },
  });

  function handleCreate() {
    openModal(true, {
      isUpdate: false,
    });
  }

  function handleEdit(record: Recordable) {
    openModal(true, {
      record,
      isUpdate: true,
    });
  }

  async function handleDelete(record: Recordable) {
    await deleteOne({ id: record.id }, reload);
  }
</script>
