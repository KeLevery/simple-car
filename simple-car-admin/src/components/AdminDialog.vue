<script setup lang="ts">
defineProps<{
  title: string
  open: boolean
  submitText?: string
}>()

const emit = defineEmits<{
  close: []
  submit: []
}>()
</script>

<template>
  <Teleport to="body">
    <div v-if="open" class="dialog-mask">
      <section class="dialog-panel" role="dialog" aria-modal="true">
        <header class="dialog-header">
          <h2>{{ title }}</h2>
          <button class="icon-button" type="button" title="关闭" @click="emit('close')">×</button>
        </header>
        <form class="dialog-form" @submit.prevent="emit('submit')">
          <slot></slot>
          <footer class="dialog-actions">
            <button class="text-button" type="button" @click="emit('close')">取消</button>
            <button class="primary-button compact" type="submit">{{ submitText || '保存' }}</button>
          </footer>
        </form>
      </section>
    </div>
  </Teleport>
</template>
