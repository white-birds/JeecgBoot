<template>
  <PageWrapper title="发起审批" content="选择您需要申请的审批类型，快速发起流程。">
    <div class="p-4 flex flex-wrap justify-center">
      <template v-for="item in approvalTypes" :key="item.title">
        <Card :title="item.title" class="approval-card m-4" hoverable @click="handleCreate(item.value)">
          <template #extra>
            <Icon :icon="item.icon" :size="30" :color="item.color" />
          </template>
          <div class="text-secondary">{{ item.description }}</div>
          <div class="mt-4 flex justify-end">
            <a-button type="primary" shape="round">立即发起</a-button>
          </div>
        </Card>
      </template>
    </div>
    <AppProjectModal @register="registerModal" @success="handleSuccess" />
  </PageWrapper>
</template>

<script lang="ts" setup>
  import { Card, Button as AButton } from 'ant-design-vue';
  import { PageWrapper } from '/@/components/Page';
  import Icon from '/@/components/Icon/index'; // 恢复导入
  import { useModal } from '/@/components/Modal';
  import AppProjectModal from './components/AppProjectModal.vue';
  import { useGo } from '/@/hooks/web/usePage';
  // 移除 Icon 的显式导入，通常 JeecgBoot 已经全局注册了 Icon 组件
  // 如果报错找不到 Icon，再尝试 import Icon from '/@/components/Icon/Icon.vue'

  const [registerModal, { openModal }] = useModal();
  const go = useGo();

  const approvalTypes = [
    {
      title: '请假申请',
      value: '请假',
      icon: 'ant-design:calendar-outlined',
      color: '#1890ff',
      description: '包含年假、病假、事假等各类假期申请。',
    },
    {
      title: '报销申请',
      value: '报销',
      icon: 'ant-design:pay-circle-outlined',
      color: '#722ed1',
      description: '差旅费、加班费、办公用品等费用报销。',
    },
    {
      title: '加班申请',
      value: '加班',
      icon: 'ant-design:field-time-outlined',
      color: '#13c2c2',
      description: '工作日加班、节假日加班申请及补休登记。',
    },
  ];

  function handleCreate(type: string) {
    openModal(true, {
      isUpdate: false,
      record: { type },
    });
  }

  function handleSuccess() {
    // 提交成功后跳转到“我的申请”页面查看进度
    go('/approval/MyApplicationList');
  }
</script>

<style lang="less" scoped>
  .approval-card {
    width: 300px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
  }

  .text-secondary {
    color: rgba(0, 0, 0, 0.45);
    font-size: 14px;
    line-height: 1.5;
    height: 42px;
    overflow: hidden;
  }
</style>
